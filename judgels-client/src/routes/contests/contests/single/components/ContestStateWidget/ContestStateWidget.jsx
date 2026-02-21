import { Alert, Button, Callout, Intent } from '@blueprintjs/core';
import { InfoSign, Time } from '@blueprintjs/icons';
import { useMutation, useQuery, useQueryClient, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';

import { ButtonLink } from '../../../../../../components/ButtonLink/ButtonLink';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { ContestState } from '../../../../../../modules/api/uriel/contestWeb';
import {
  contestBySlugQueryOptions,
  searchProblemSetQueryOptions,
  startVirtualContestMutationOptions,
} from '../../../../../../modules/queries/contest';
import { contestWebConfigQueryOptions } from '../../../../../../modules/queries/contestWeb';

// TODO(fushar): unit tests
export default function ContestStateWidget() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { data: webConfig } = useSuspenseQuery(contestWebConfigQueryOptions(contestSlug));
  const queryClient = useQueryClient();

  const contestState = webConfig.state;
  const remainingStateDuration = webConfig.remainingStateDuration;

  const startVirtualMutation = useMutation(startVirtualContestMutationOptions(contest.jid));

  const { data: problemSet } = useQuery({
    ...searchProblemSetQueryOptions(contest.jid),
    enabled: contestState === ContestState.Finished,
  });

  const [state, setState] = useState({
    baseRemainingDuration: undefined,
    baseTimeForRemainingDuration: undefined,
    remainingDuration: undefined,
    isVirtualContestAlertOpen: undefined,
  });

  const currentTimeoutRef = useRef(null);

  useEffect(() => {
    setUpBaseRemainingDuration();
    refreshRemainingDuration();

    return () => {
      if (currentTimeoutRef.current) {
        clearTimeout(currentTimeoutRef.current);
      }
    };
  }, []);

  useEffect(() => {
    setUpBaseRemainingDuration();
  }, [remainingStateDuration]);

  const renderVirtualContestAlert = () => (
    <Alert
      isOpen={state.isVirtualContestAlertOpen || false}
      confirmButtonText="Yes, start my participation"
      onConfirm={startVirtualContest}
      cancelButtonText="Cancel"
      onCancel={cancelVirtualContest}
      intent={Intent.WARNING}
      icon={<Time />}
    >
      Are you sure you want to start your participation in this contest?
    </Alert>
  );

  const renderUpsolveButton = () => {
    if (!problemSet) {
      return null;
    }
    return (
      <>
        &nbsp;&nbsp;
        <ButtonLink small intent={Intent.WARNING} to={`/problems/${problemSet.slug}`}>
          Upsolve problems
        </ButtonLink>
      </>
    );
  };

  const getWidgetComponents = () => {
    if (contestState === ContestState.NotBegun) {
      return {
        leftComponent: <span>Contest hasn't started yet.</span>,
        rightComponent: !!state.remainingDuration && <span>Starts in {getRemainingDuration()}</span>,
      };
    }
    if (contestState === ContestState.Begun) {
      return {
        leftComponent: (
          <Button small intent={Intent.WARNING} onClick={alertVirtualContest} loading={startVirtualMutation.isPending}>
            Click here to start your participation
          </Button>
        ),
        rightComponent: !!state.remainingDuration && <span>Ends in {getRemainingDuration()}</span>,
      };
    }
    if (contestState === ContestState.Started) {
      return {
        leftComponent: <span>Contest is running.</span>,
        rightComponent: !!state.remainingDuration && <span>Ends in {getRemainingDuration()}</span>,
      };
    }
    if (contestState === ContestState.Finished) {
      return {
        leftComponent: (
          <>
            <span>Contest is over.</span>
            {renderUpsolveButton()}
          </>
        ),
      };
    }
    if (contestState === ContestState.Paused) {
      return {
        leftComponent: <span>Contest is paused.</span>,
      };
    }
    return {};
  };

  const getRemainingDuration = () => {
    return <FormattedDuration value={state.remainingDuration} />;
  };

  const refreshRemainingDuration = () => {
    setState(prevState => {
      const {
        remainingDuration: prevRemainingDuration,
        baseRemainingDuration,
        baseTimeForRemainingDuration,
      } = prevState;
      const remainingDuration = Math.max(
        0,
        baseRemainingDuration + baseTimeForRemainingDuration - new Date().getTime()
      );

      if (remainingDuration === 0 && prevRemainingDuration !== 0) {
        queryClient.invalidateQueries(contestWebConfigQueryOptions(contestSlug));
      }

      return { ...prevState, remainingDuration };
    });

    currentTimeoutRef.current = setTimeout(refreshRemainingDuration, 500);
  };

  const setUpBaseRemainingDuration = () => {
    setState(prevState => ({
      ...prevState,
      baseRemainingDuration: remainingStateDuration,
      baseTimeForRemainingDuration: new Date().getTime(),
    }));
  };

  const alertVirtualContest = () => {
    setState(prevState => ({ ...prevState, isVirtualContestAlertOpen: true }));
  };

  const cancelVirtualContest = () => {
    setState(prevState => ({ ...prevState, isVirtualContestAlertOpen: false }));
  };

  const startVirtualContest = async () => {
    setState(prevState => ({ ...prevState, isVirtualContestAlertOpen: false }));
    await startVirtualMutation.mutateAsync();
    await queryClient.invalidateQueries(contestWebConfigQueryOptions(contestSlug));
  };

  const { leftComponent, rightComponent } = getWidgetComponents();
  return (
    <Callout intent={Intent.PRIMARY} className="secondary-info" icon={<InfoSign />}>
      <div className="float-left">{leftComponent}</div>
      <div className="float-right">{rightComponent}</div>
      <div className="clearfix" />
      {renderVirtualContestAlert()}
    </Callout>
  );
}
