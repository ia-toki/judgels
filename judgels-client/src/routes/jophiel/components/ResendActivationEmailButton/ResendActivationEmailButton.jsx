import { Button, Intent } from '@blueprintjs/core';
import { useMutation } from '@tanstack/react-query';
import { useEffect, useRef, useState } from 'react';

import { userAccountAPI } from '../../../../modules/api/jophiel/userAccount';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function ResendActivationEmailButton({ email }) {
  const timerRef = useRef(null);
  const [timeRemaining, setTimeRemaining] = useState(5);

  const resendMutation = useMutation({
    mutationFn: () => userAccountAPI.resendActivationEmail(email),
  });

  useEffect(() => {
    timerRef.current = setInterval(countDown, 1000);
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
        timerRef.current = null;
      }
    };
  }, []);

  const onResendEmail = async () => {
    await resendMutation.mutateAsync(undefined, {
      onSuccess: () => {
        toastActions.showToast('Email has been sent');
      },
    });
    setTimeRemaining(5);
    timerRef.current = setInterval(countDown, 1000);
  };

  const countDown = () => {
    setTimeRemaining(prev => {
      const seconds = prev - 1;
      if (seconds === 0 && timerRef.current) {
        clearInterval(timerRef.current);
      }
      return seconds;
    });
  };

  return (
    <Button
      type="submit"
      text={`Resend activation email${timeRemaining > 0 ? ` (${timeRemaining})` : ''}`}
      intent={Intent.PRIMARY}
      onClick={onResendEmail}
      disabled={timeRemaining > 0}
    />
  );
}
