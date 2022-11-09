export function constructContainerUrl(subpaths) {
  if (!subpaths) {
    return '';
  }
  if (subpaths.length === 2) {
    return `/courses/${subpaths[0]}/chapters/${subpaths[1]}/problems`;
  } else {
    return `/problems/${subpaths[0]}`;
  }
}
