import { ProgressBar } from '@blueprintjs/core';

import { SingleColumnLayout } from '../SingleColumnLayout/SingleColumnLayout';
import { useShowAfterDelay } from './useShowAfterDelay';

import './LoadingState.scss';

export function LoadingState({ large }) {
  const showProgressBar = useShowAfterDelay(500);

  const progressBar = showProgressBar ? <ProgressBar className="loading-state" /> : null;
  return large ? <SingleColumnLayout>{progressBar}</SingleColumnLayout> : progressBar;
}
