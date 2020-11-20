import { Intent, Position, Toaster } from '@blueprintjs/core';
import { SubmissionError } from 'redux-form';
import { ForbiddenError, NotFoundError, RemoteError } from '../api/error';

const toaster = Toaster.create({
  position: Position.TOP,
  className: 'toast',
});

export function showToast(message) {
  toaster.show({
    message,
    intent: Intent.NONE,
    timeout: 1500,
  });
}

export function showSuccessToast(message) {
  toaster.show({
    icon: 'tick',
    message,
    intent: Intent.SUCCESS,
    timeout: 1500,
  });
}

export function showAlertToast(message) {
  toaster.show({
    icon: 'envelope',
    message,
    intent: Intent.WARNING,
    timeout: 0,
  });
}

export function showErrorToast(error) {
  let message;
  if (error instanceof RemoteError) {
    message = 'Internal server error; please try again later.';
  } else if (error instanceof ForbiddenError) {
    message = 'Operation not allowed.';
  } else if (error instanceof NotFoundError) {
    message = 'Resource not found.';
  } else if (error instanceof SubmissionError) {
    message = error.errors['_error'] || error.message;
  } else {
    message = error.message;
  }

  toaster.show({
    icon: 'warning-sign',
    message,
    intent: Intent.DANGER,
  });
}

export const toastActions = {
  showToast,
  showSuccessToast,
  showAlertToast,
  showErrorToast,
};
