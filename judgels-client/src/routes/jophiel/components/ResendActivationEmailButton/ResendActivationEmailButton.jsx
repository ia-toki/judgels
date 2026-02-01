import { Button, Intent } from '@blueprintjs/core';
import { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';

import * as userAccountActions from '../../modules/userAccountActions';

export default function ResendActivationEmailButton({ email }) {
  const dispatch = useDispatch();
  const timerRef = useRef(null);

  const [state, setState] = useState({
    timeRemainingResendEmail: 5,
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

  const render = () => {
    const { timeRemainingResendEmail } = state;
    return (
      <Button
        type="submit"
        text={`Resend activation email${timeRemainingResendEmail > 0 ? ` (${timeRemainingResendEmail})` : ''}`}
        intent={Intent.PRIMARY}
        onClick={onResendEmail}
        disabled={timeRemainingResendEmail > 0}
      />
    );
  };

  const onResendEmail = async () => {
    await dispatch(userAccountActions.resendActivationEmail(email));
    setState(prevState => ({ ...prevState, timeRemainingResendEmail: 5 }));
    timerRef.current = setInterval(countDown, 1000);
  };

  const countDown = () => {
    setState(prevState => {
      const seconds = prevState.timeRemainingResendEmail - 1;
      if (seconds === 0 && timerRef.current) {
        clearInterval(timerRef.current);
      }
      return { ...prevState, timeRemainingResendEmail: seconds };
    });
  };

  return render();
}
