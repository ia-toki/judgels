import logo from '../../assets/images/logo-header.png';

export function showDesktopNotification(title, tag, message) {
  new Notification(title, { body: message, icon: logo, tag: tag });
}

export function askDesktopNotificationPermission() {
  Notification.requestPermission(result => {});
}
