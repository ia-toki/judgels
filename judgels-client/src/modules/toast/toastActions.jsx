import { Intent, OverlayToaster, Position } from '@blueprintjs/core';
import { Envelope, Tick, WarningSign } from '@blueprintjs/icons';

import { ForbiddenError, NotFoundError, RemoteError } from '../api/error';

const toasterPromise = OverlayToaster.create({
  position: Position.TOP,
  className: 'toast',
});

export function showToast(message) {
  toasterPromise.then(toaster =>
    toaster.show({
      message,
      intent: Intent.NONE,
      timeout: 1500,
    })
  );
}

export function showSuccessToast(message) {
  toasterPromise.then(toaster =>
    toaster.show({
      icon: <Tick />,
      message,
      intent: Intent.SUCCESS,
      timeout: 1500,
    })
  );
}

export function showAlertToast(message) {
  toasterPromise.then(toaster =>
    toaster.show({
      icon: <Envelope />,
      message,
      intent: Intent.WARNING,
      timeout: 0,
    })
  );
}

export function showErrorToast(error) {
  let message;
  if (error instanceof RemoteError) {
    message = 'Internal server error; please try again later.';
  } else if (error instanceof ForbiddenError) {
    message = 'Operation not allowed.';
  } else if (error instanceof NotFoundError) {
    message = 'Resource not found.';
  } else {
    message = error.message;
  }

  toasterPromise.then(toaster =>
    toaster.show({
      icon: <WarningSign />,
      message,
      intent: Intent.DANGER,
    })
  );
}

export const toastActions = {
  showToast,
  showSuccessToast,
  showAlertToast,
  showErrorToast,
};
