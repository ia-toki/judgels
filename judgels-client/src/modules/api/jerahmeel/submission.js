export function constructContainerUrl(subpaths) {
  if (!subpaths) {
    return '';
  }
  if (subpaths.length === 2) {
    return `/courses/${subpaths[0]}/chapters/${subpaths[1]}`;
  } else {
    return `/problems/${subpaths[0]}`;
  }
}

export function constructProblemUrl(subpaths, problemAlias) {
  if (!subpaths) {
    return '';
  }
  if (subpaths.length === 2) {
    return `${constructContainerUrl(subpaths)}/problems/${problemAlias}`;
  } else {
    return `${constructContainerUrl(subpaths)}/${problemAlias}`;
  }
}
