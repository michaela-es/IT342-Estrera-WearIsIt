import { useState, useEffect, useRef } from 'react';

export const useCache = (
  key,
  fetchFn,
  duration = 5 * 60 * 1000,
  options = {}
) => {
  const { enabled = true, dependencies = [] } = options;

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const abortRef = useRef(false);

  useEffect(() => {
    abortRef.current = false;

    const loadData = async () => {
      setLoading(true);
      setError(null);

      try {
        if (!enabled) return;

        const cached = sessionStorage.getItem(key);

        if (cached) {
          const { data: cachedData, timestamp } = JSON.parse(cached);
          const isExpired = Date.now() - timestamp > duration;

          if (!isExpired) {
            setData(cachedData);
            setLoading(false);

            fetchAndUpdate();
            return;
          }
        }

        await fetchAndUpdate();
      } catch (err) {
        if (!abortRef.current) setError(err);
      } finally {
        if (!abortRef.current) setLoading(false);
      }
    };

    const fetchAndUpdate = async () => {
      const freshData = await fetchFn();

      if (abortRef.current) return;

      setData(freshData);

      sessionStorage.setItem(
        key,
        JSON.stringify({
          data: freshData,
          timestamp: Date.now(),
        })
      );
    };

    loadData();

    return () => {
      abortRef.current = true;
    };
  }, [key, duration, enabled, ...dependencies]);

  const invalidate = () => {
    sessionStorage.removeItem(key);
  };

  const refresh = async () => {
    sessionStorage.removeItem(key);

    const freshData = await fetchFn();
    setData(freshData);

    sessionStorage.setItem(
      key,
      JSON.stringify({
        data: freshData,
        timestamp: Date.now(),
      })
    );
  };

  return {
    data,
    loading,
    error,
    invalidate,
    refresh,
  };
};