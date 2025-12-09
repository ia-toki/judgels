import { toastActions } from '../toast/toastActions';

export const isClipboardWriteTextSupported = () => {
  return !!(navigator.clipboard && navigator.clipboard.writeText);
};

export function copyTextToClipboard(text) {
  if (!isClipboardWriteTextSupported()) {
    return;
  }
  navigator.clipboard.writeText(text).then(
    () => toastActions.showSuccessToast('Source code copied.'),
    () => toastActions.showErrorToast({ message: 'Failed to copy.' })
  );
}
