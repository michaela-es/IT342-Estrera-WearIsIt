class GoogleAuthService {
  constructor() {
    this.initialized = false;
    this.callback = null;
  }

  initialize(clientId, callback) {
    if (!this.initialized && window.google) {
      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: callback,
        auto_select: false,
        cancel_on_tap_outside: true,
      });
      this.initialized = true;
      this.callback = callback;
      console.log("Google Auth initialized (singleton)");
    }
  }

  renderButton(element, options) {
    if (window.google && this.initialized) {
      window.google.accounts.id.renderButton(element, options);
    }
  }
}

export default new GoogleAuthService();