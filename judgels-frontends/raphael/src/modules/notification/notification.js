import logo from '../../assets/images/logo-header.png';
import { APP_CONFIG } from '../../conf';

export function showDesktopNotification(title, tag, message) {
  new Notification(title, { body: `${APP_CONFIG.name}: ${message}`, icon: logo, tag: tag });
}

export function askDesktopNotificationPermission() {
  Notification.requestPermission(result => {});
}
