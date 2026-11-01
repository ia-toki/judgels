import { Alert, Intent } from '@blueprintjs/core';
import { WarningSign } from '@blueprintjs/icons';
import { useBlocker } from '@tanstack/react-router';
import { useFormState } from 'react-final-form';

const isLeavingPage = ({ current, next }) => current.pathname !== next.pathname;

export function UnsavedChangesAlert({ when }) {
  const { status, proceed, reset } = useBlocker({
    shouldBlockFn: isLeavingPage,
    disabled: !when,
    withResolver: true,
  });

  return (
    <Alert
      isOpen={status === 'blocked'}
      intent={Intent.DANGER}
      icon={<WarningSign />}
      confirmButtonText="Discard"
      cancelButtonText="Keep editing"
      canEscapeKeyCancel
      onConfirm={() => proceed?.()}
      onCancel={() => reset?.()}
    >
      Discard your unsaved changes?
    </Alert>
  );
}

export function FormUnsavedChangesAlert() {
  const { dirty, submitting } = useFormState({ subscription: { dirty: true, submitting: true } });

  return <UnsavedChangesAlert when={dirty && !submitting} />;
}
