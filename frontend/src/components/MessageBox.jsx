import React from 'react';

const MessageBox = ({ type, text, onClose }) => {
  if (!text) return null;

  const getStyles = () => {
    switch (type) {
      case 'success':
        return 'bg-green-100 border-green-500 text-green-700';
      case 'error':
        return 'bg-red-100 border-red-500 text-red-700';
      case 'info':
        return 'bg-blue-100 border-blue-500 text-blue-700';
      default:
        return 'bg-gray-100 border-gray-500 text-gray-700';
    }
  };

  const getIconColor = () => {
    switch (type) {
      case 'success':
        return 'var(--success-color, #22c55e)'; 
      case 'error':
        return 'var(--error-color, #ef4444)'; 
      case 'info':
        return 'var(--info-color, #3b82f6)';   
      default:
        return 'var(--text-secondary, #6b7280)';
    }
  };

  const getIcon = () => {
    const iconColor = getIconColor();
    
    switch (type) {
      case 'success':
        return (
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-6 w-6 shrink-0 stroke-current"
            fill="none"
            viewBox="0 0 24 24"
            style={{ stroke: iconColor }}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth="2"
              d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
        );
      default:
        return null;
    }
  };

  return (
    <div
      role="alert"
      className={`flex items-center gap-2 border-l-4 p-4 rounded-md ${getStyles()}`}
    >
      {getIcon()}
      <span className="flex-1">{text}</span>
      {onClose && (
        <button
          type="button"
          onClick={onClose}
          className="ml-2 text-gray-500 hover:text-gray-700"
          aria-label="Close message"
        >
          ×
        </button>
      )}
    </div>
  );
};

export default MessageBox;