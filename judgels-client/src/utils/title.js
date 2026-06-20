import { getAppName } from '../modules/webConfig';

export function createDocumentTitle(title) {
  const appName = getAppName();
  return title ? `${title} | ${appName}` : appName;
}
