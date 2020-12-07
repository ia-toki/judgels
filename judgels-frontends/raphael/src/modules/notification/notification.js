
import logo from '../../assets/images/logo-header.png';

export function showNotification(title, message) {
  new Notification(title, {body: message, icon: logo});
}

export function askNotificationPermission() {
  Notification.requestPermission(result => {});
}
