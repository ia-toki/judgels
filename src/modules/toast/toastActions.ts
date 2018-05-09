import { ForbiddenError, RemoteError } from '../api/error';
import { Intent, Position, Toaster } from '@blueprintjs/core';

export function createToastActions(toaster) {
  return {
    showToast: (message: string) => {
      toaster.show({
        message,
        intent: Intent.NONE,
      });
    },

    showSuccessToast: (message: string) => {
      toaster.show({
        icon: 'tick',
        message,
        intent: Intent.SUCCESS,
      });
    },

    showErrorToast: (error: any) => {
      let message: string;
      if (error instanceof RemoteError) {
        message = 'Internal server error; please try again later.';
      } else if (error instanceof ForbiddenError) {
        message = 'You are not allowed to view this resource.';
      } else {
        message = error.message;
      }

      toaster.show({
        icon: 'warning-sign',
        message,
        intent: Intent.DANGER,
      });
    },
  };
}

const injectedToaster = Toaster.create({
  position: Position.TOP,
  className: 'toast',
});

export const toastActions = createToastActions(injectedToaster);
