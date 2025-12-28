// Navigation reference for use outside React components (e.g., Redux actions)
// This allows imperative navigation without hooks

let navigationRef = {
  push: null,
  replace: null,
};

export function setNavigationRef(nav) {
  navigationRef = nav;
}

export function getNavigationRef() {
  return navigationRef;
}
