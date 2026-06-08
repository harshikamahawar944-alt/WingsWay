(function () {
  const params = new URLSearchParams(window.location.search);

  function getToken() {
    return localStorage.getItem("jwtToken");
  }

  function decodePayload(token) {
    try {
      let payload = token.split(".")[1];
      if (!payload) {
        return null;
      }
      payload = payload.replace(/-/g, "+").replace(/_/g, "/");
      payload += "=".repeat((4 - (payload.length % 4)) % 4);
      return JSON.parse(atob(payload));
    } catch (error) {
      return null;
    }
  }

  function isTokenExpired(token) {
    if (!token) {
      return true;
    }

    const payload = decodePayload(token);
    if (!payload || !payload.exp) {
      return true;
    }

    return payload.exp * 1000 <= Date.now();
  }

  function clearAuth() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    document.cookie = "jwtToken=; path=/; max-age=0; SameSite=Lax";
  }

  function syncAuthCookie() {
    const token = getToken();
    if (!token || isTokenExpired(token)) {
      clearAuth();
      return false;
    }

    document.cookie = `jwtToken=${encodeURIComponent(token)}; path=/; max-age=86400; SameSite=Lax`;
    return true;
  }

  function requireAuth(loginPath) {
    const target = loginPath || "/api/v1/auth/login";
    if (!syncAuthCookie()) {
      window.location.href = target;
      return false;
    }
    return true;
  }

  window.authUtils = {
    getToken,
    isTokenExpired,
    clearAuth,
    syncAuthCookie,
    requireAuth
  };

  if (params.get("logout") === "true") {
    clearAuth();
    window.history.replaceState({}, document.title, window.location.pathname);
  } else {
    syncAuthCookie();
  }
})();
