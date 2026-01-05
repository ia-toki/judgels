import { APP_CONFIG } from '../conf';

export function createDocumentTitle(title) {
  return title ? `${title} | ${APP_CONFIG.name}` : APP_CONFIG.name;
}
