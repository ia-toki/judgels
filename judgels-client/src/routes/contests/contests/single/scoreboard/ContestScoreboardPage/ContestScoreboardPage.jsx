import { Button, Callout, Intent, Switch } from '@blueprintjs/core';
import { Pause, Refresh } from '@blueprintjs/icons';
import { useLocation, useNavigate } from '@tanstack/react-router';
import { Fragment, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { SubmissionImageDialog } from '../../../../../../components/SubmissionImageDialog/SubmissionImageDialog';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import { selectMaybeUserJid } from '../../../../../../modules/session/sessionSelectors';
import { selectContest } from '../../../modules/contestSelectors';
import { BundleScoreboardTable } from '../BundleScoreboardTable/BundleScoreboardTable';
import ContestUserProblemSubmissionsDialog from '../ContestUserProblemSubmissionsDialog/ContestUserProblemSubmissionsDialog';
import { GcjScoreboardTable } from '../GcjScoreboardTable/GcjScoreboardTable';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { IoiScoreboardTable } from '../IoiScoreboardTable/IoiScoreboardTable';
import { TrocScoreboardTable } from '../TrocScoreboardTable/TrocScoreboardTable';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

import './ContestScoreboardPage.scss';

const PAGE_SIZE = 250;

function ContestScoreboardPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const userJid = useSelector(selectMaybeUserJid);
  const contest = useSelector(selectContest);

  const frozen = !!location.search.frozen;
  const showClosedProblems = !!location.search.showClosedProblems;

  const [state, setState] = useState({
    response: undefined,
    isForceRefreshButtonLoading: false,
    isSubmissionImageDialogOpen: false,
    isSubmissionsDialogOpen: false,
    submissionImageUrl: undefined,
    submissionDialogTitle: '',
    submissionsDialogProps: undefined,
  });

  const render = () => {
    return (
      <ContentCard className="contest-scoreboard-page">
        <h3>Scoreboard</h3>
        {renderScoreboardUpdatedTime()}
        <div className="clearfix" />
        <hr />
        <div className="content-card__header">
          {renderFilter()}
          {renderForceRefreshButton()}
          <div className="clearfix" />
        </div>
        {renderFrozenScoreboardNotice()}
        {renderScoreboard()}
        <Pagination key={'' + frozen + showClosedProblems} pageSize={PAGE_SIZE} onChangePage={onChangePage} />
        {state.isSubmissionImageDialogOpen && (
          <SubmissionImageDialog
            onClose={toggleSubmissionImageDialog}
            title={state.submissionDialogTitle}
            imageUrl={state.submissionImageUrl}
          />
        )}
        {state.isSubmissionsDialogOpen && (
          <ContestUserProblemSubmissionsDialog onClose={toggleSubmissionsDialog} {...state.submissionsDialogProps} />
        )}
      </ContentCard>
    );
  };

  const toggleSubmissionImageDialog = () => {
    setState(prevState => ({ isSubmissionImageDialogOpen: !prevState.isSubmissionImageDialogOpen }));
  };

  const toggleSubmissionsDialog = props => {
    setState(prevState => ({
      isSubmissionsDialogOpen: !prevState.isSubmissionsDialogOpen,
      submissionsDialogProps: props,
    }));
  };

  const onChangePage = async nextPage => {
    const scoreboard = await refreshScoreboard(nextPage);
    if (scoreboard) {
      return scoreboard.data.totalEntries;
    } else {
      return 0;
    }
  };

  const refreshScoreboard = async nextPage => {
    const response = await dispatch(
      contestScoreboardActions.getScoreboard(contest.jid, frozen, showClosedProblems, nextPage)
    );
    setState(prevState => ({ ...prevState, response: response ? [response] : [] }));
    return response;
  };

  const renderScoreboardUpdatedTime = () => {
    const { response } = state;
    if (!response || response.length === 0) {
      return null;
    }

    const scoreboard = response[0].data;
    const contestEndTime = contest.beginTime + contest.duration;
    return (
      <p className="contest-scoreboard-page__info">
        <small>
          last updated <FormattedRelative value={Math.min(contestEndTime, scoreboard.updatedTime)} />
        </small>
      </p>
    );
  };

  const renderFrozenScoreboardNotice = () => {
    const { response } = state;
    if (!response || response.length === 0 || response[0].data.type !== ContestScoreboardType.Frozen) {
      return null;
    }

    return (
      <Callout
        className="contest-scoreboard-page__frozen"
        icon={<Pause />}
        intent={Intent.WARNING}
        title="SCOREBOARD HAS BEEN FROZEN"
      />
    );
  };

  const renderFilter = () => {
    const { response } = state;
    if (!response || response.length === 0) {
      return null;
    }

    const { canViewOfficialAndFrozen, canViewClosedProblems } = response[0].config;
    if (!canViewOfficialAndFrozen && !canViewClosedProblems) {
      return null;
    }

    return (
      <div className="float-left">
        {canViewOfficialAndFrozen && (
          <Switch
            className="contest-scoreboard-page__filter"
            label="Frozen"
            checked={frozen}
            onChange={onChangeFrozen}
          />
        )}
        {canViewClosedProblems && (
          <Switch
            className="contest-scoreboard-page__filter"
            label="Show closed problems"
            checked={showClosedProblems}
            onChange={onChangeShowClosedProblems}
          />
        )}
        <div className="clearfix " />
      </div>
    );
  };

  const renderForceRefreshButton = () => {
    const { response } = state;
    if (!response || response.length === 0) {
      return null;
    }

    const { canRefresh } = response[0].config;
    if (!canRefresh) {
      return null;
    }

    return (
      <Button
        className="float-right"
        intent="primary"
        small
        icon={<Refresh />}
        onClick={forceRefreshScoreboard}
        loading={!!state.isForceRefreshButtonLoading}
      >
        Force refresh
      </Button>
    );
  };

  const forceRefreshScoreboard = async () => {
    setState(prevState => ({ ...prevState, isForceRefreshButtonLoading: true }));
    await dispatch(contestScoreboardActions.refreshScoreboard(contest.jid));
    setState(prevState => ({ ...prevState, isForceRefreshButtonLoading: false }));
  };

  const onChangeFrozen = ({ target }) => onChange(target.checked, showClosedProblems);

  const onChangeShowClosedProblems = ({ target }) => onChange(frozen, target.checked);

  const onChange = (frozenVal, showClosedProblemsVal) => {
    let newQueries = { ...location.search, frozen: undefined, showClosedProblems: undefined };
    if (frozenVal) {
      newQueries = { ...newQueries, frozen: frozenVal };
    }
    if (showClosedProblemsVal) {
      newQueries = { ...newQueries, showClosedProblems: showClosedProblemsVal };
    }
    navigate({ search: newQueries });
  };

  const openSubmissionsDialog = (contestantJid, problemJid) => {
    const {
      profilesMap,
      data: { scoreboard },
    } = state.response[0];

    const profile = profilesMap[contestantJid];

    const problemIndex = scoreboard.state.problemJids.indexOf(problemJid);
    const problemAlias = scoreboard.state.problemAliases[problemIndex];

    toggleSubmissionsDialog({
      userJid: contestantJid,
      problemJid,
      title: `Submissions by ${profile.username} for problem ${problemAlias}`,
    });
  };

  const openSubmissionImage = async (contestantJid, problemJid) => {
    const {
      data: { scoreboard },
    } = state.response[0];

    const problemIndex = scoreboard.state.problemJids.indexOf(problemJid);
    const problemAlias = scoreboard.state.problemAliases[problemIndex];

    const [info, submissionImageUrl] = await Promise.all([
      dispatch(contestScoreboardActions.getSubmissionInfo(contest.jid, contestantJid, problemJid)),
      dispatch(contestScoreboardActions.getSubmissionSourceImage(contest.jid, contestantJid, problemJid)),
    ]);
    const submissionDialogTitle = `Submission #${info.id} by ${info.profile.username} for problem ${problemAlias}`;
    setState(prevState => ({ ...prevState, submissionImageUrl, submissionDialogTitle }));
    toggleSubmissionImageDialog();
  };

  const openSubmissionSummary = contestantJid => {
    const { profilesMap } = state.response[0];

    const profile = profilesMap[contestantJid];
    navigate({ to: `/contests/${contest.slug}/submissions/users/${profile.username}` });
  };

  const renderScoreboard = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }
    if (response.length === 0) {
      return (
        <p>
          <small>No scoreboard.</small>
        </p>
      );
    }

    const {
      data: scoreboard,
      profilesMap,
      config: { canViewSubmissions, canViewSubmissionDetails },
    } = response[0];

    let onClickSubmissionCell;
    if (canViewSubmissionDetails) {
      onClickSubmissionCell = openSubmissionsDialog;
    } else if (canViewSubmissions) {
      onClickSubmissionCell = openSubmissionImage;
    }

    if (contest.style === ContestStyle.TROC) {
      return (
        <TrocScoreboardTable
          userJid={userJid}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          canViewSubmissions={canViewSubmissions}
          onClickSubmissionCell={onClickSubmissionCell}
        />
      );
    } else if (contest.style === ContestStyle.ICPC) {
      return (
        <IcpcScoreboardTable
          userJid={userJid}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          onClickSubmissionCell={onClickSubmissionCell}
        />
      );
    } else if (contest.style === ContestStyle.IOI) {
      return (
        <IoiScoreboardTable
          userJid={userJid}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          onClickSubmissionCell={onClickSubmissionCell}
        />
      );
    } else if (contest.style === ContestStyle.Bundle) {
      return (
        <BundleScoreboardTable
          userJid={userJid}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          onClickTotalScoresCell={openSubmissionSummary}
        />
      );
    } else if (contest.style === ContestStyle.GCJ) {
      return (
        <GcjScoreboardTable
          userJid={userJid}
          scoreboard={scoreboard.scoreboard}
          profilesMap={profilesMap}
          onClickSubmissionCell={onClickSubmissionCell}
        />
      );
    } else {
      return <Fragment />;
    }
  };

  return render();
}

export default ContestScoreboardPage;
