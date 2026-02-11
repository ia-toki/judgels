// https://www.codemzy.com/blog/fix-chunkloaderror-react

// For use with React.lazy()
export const lazyRetry = function (componentImport) {
  return new Promise((resolve, reject) => {
    const hasRefreshed = JSON.parse(window.sessionStorage.getItem('retry-lazy-refreshed') || 'false');
    componentImport()
      .then(component => {
        window.sessionStorage.setItem('retry-lazy-refreshed', 'false');
        resolve(component);
      })
      .catch(error => {
        if (!hasRefreshed) {
          window.sessionStorage.setItem('retry-lazy-refreshed', 'true');
          return window.location.reload();
        }
        reject(error);
      });
  });
};

export function isChunkLoadError(error) {
  const message = error?.message || '';
  return (
    message.includes('Failed to fetch dynamically imported module') ||
    message.includes('Loading chunk') ||
    message.includes('Loading CSS chunk') ||
    message.includes('Unable to preload CSS') ||
    error?.name === 'ChunkLoadError'
  );
}

// For use with TanStack Router's lazyRouteComponent()
export const retryImport = importFn => () => {
  return importFn().then(module => {
    window.sessionStorage.setItem('retry-lazy-refreshed', 'false');
    return module;
  }).catch(error => {
    if (!isChunkLoadError(error)) throw error;

    const hasRefreshed = JSON.parse(window.sessionStorage.getItem('retry-lazy-refreshed') || 'false');
    if (!hasRefreshed) {
      window.sessionStorage.setItem('retry-lazy-refreshed', 'true');
      window.location.reload();
      return new Promise(() => {}); // never resolves; page will reload
    }
    window.sessionStorage.setItem('retry-lazy-refreshed', 'false');
    throw error;
  });
};
