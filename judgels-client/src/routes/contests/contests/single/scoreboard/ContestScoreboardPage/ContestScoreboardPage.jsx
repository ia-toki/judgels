import { Button, Callout, Intent, Switch } from '@blueprintjs/core';
import { Pause, Refresh } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { Fragment, useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { FormattedRelative } from '../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { SubmissionImageDialog } from '../../../../../../components/SubmissionImageDialog/SubmissionImageDialog';
import { ContestStyle } from '../../../../../../modules/api/uriel/contest';
import { ContestScoreboardType } from '../../../../../../modules/api/uriel/contestScoreboard';
import { contestSubmissionProgrammingAPI } from '../../../../../../modules/api/uriel/contestSubmissionProgramming';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import {
  contestScoreboardQueryOptions,
  refreshContestScoreboardMutationOptions,
} from '../../../../../../modules/queries/contestScoreboard';
import { contestSubmissionInfoQueryOptions } from '../../../../../../modules/queries/contestSubmissionProgramming';
import { queryClient } from '../../../../../../modules/queryClient';
import { useSession } from '../../../../../../modules/session';
import { getWebPrefs } from '../../../../../../modules/webPrefs';
import { BundleScoreboardTable } from '../BundleScoreboardTable/BundleScoreboardTable';
import ContestUserProblemSubmissionsDialog from '../ContestUserProblemSubmissionsDialog/ContestUserProblemSubmissionsDialog';
import { GcjScoreboardTable } from '../GcjScoreboardTable/GcjScoreboardTable';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { IoiScoreboardTable } from '../IoiScoreboardTable/IoiScoreboardTable';
import { TrocScoreboardTable } from '../TrocScoreboardTable/TrocScoreboardTable';

import * as toastActions from '../../../../../../modules/toast/toastActions';

import './ContestScoreboardPage.scss';

const PAGE_SIZE = 250;

function ContestScoreboardPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { user } = useSession();
  const userJid = user?.jid;
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const frozen = !!location.search.frozen;
  const showClosedProblems = !!location.search.showClosedProblems;
  const page = +(location.search.page || 1);

  const { data: response, isLoading } = useQuery(
    contestScoreboardQueryOptions(contest.jid, { frozen, showClosedProblems, page })
  );

  const refreshMutation = useMutation(refreshContestScoreboardMutationOptions(contest.jid));

  const [isSubmissionImageDialogOpen, setIsSubmissionImageDialogOpen] = useState(false);
  const [isSubmissionsDialogOpen, setIsSubmissionsDialogOpen] = useState(false);
  const [submissionImageUrl, setSubmissionImageUrl] = useState(undefined);
  const [submissionDialogTitle, setSubmissionDialogTitle] = useState('');
  const [submissionsDialogProps, setSubmissionsDialogProps] = useState(undefined);

  const renderScoreboardUpdatedTime = () => {
    if (!response) {
      return null;
    }

    const scoreboard = response.data;
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
    if (!response || response.data.type !== ContestScoreboardType.Frozen) {
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
    if (!response) {
      return null;
    }

    const { canViewOfficialAndFrozen, canViewClosedProblems } = response.config;
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
    if (!response) {
      return null;
    }

    const { canRefresh } = response.config;
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
        loading={refreshMutation.isPending}
      >
        Force refresh
      </Button>
    );
  };

  const forceRefreshScoreboard = async () => {
    await refreshMutation.mutateAsync(undefined, {
      onSuccess: () => {
        toastActions.showSuccessToast('Scoreboard refresh requested.');
      },
    });
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
    } = response;

    const profile = profilesMap[contestantJid];

    const problemIndex = scoreboard.state.problemJids.indexOf(problemJid);
    const problemAlias = scoreboard.state.problemAliases[problemIndex];

    setIsSubmissionsDialogOpen(true);
    setSubmissionsDialogProps({
      userJid: contestantJid,
      problemJid,
      title: `Submissions by ${profile.username} for problem ${problemAlias}`,
    });
  };

  const openSubmissionImage = async (contestantJid, problemJid) => {
    const {
      data: { scoreboard },
    } = response;

    const problemIndex = scoreboard.state.problemJids.indexOf(problemJid);
    const problemAlias = scoreboard.state.problemAliases[problemIndex];

    const { isDarkMode } = getWebPrefs();
    const [info, imageUrl] = await Promise.all([
      queryClient.fetchQuery(contestSubmissionInfoQueryOptions(contest.jid, contestantJid, problemJid)),
      contestSubmissionProgrammingAPI.getSubmissionSourceImage(contest.jid, contestantJid, problemJid, isDarkMode),
    ]);
    setSubmissionDialogTitle(`Submission #${info.id} by ${info.profile.username} for problem ${problemAlias}`);
    setSubmissionImageUrl(imageUrl);
    setIsSubmissionImageDialogOpen(true);
  };

  const openSubmissionSummary = contestantJid => {
    const { profilesMap } = response;

    const profile = profilesMap[contestantJid];
    navigate({ to: `/contests/${contest.slug}/submissions/users/${profile.username}` });
  };

  const renderScoreboard = () => {
    if (isLoading) {
      return <LoadingState />;
    }
    if (!response) {
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
    } = response;

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
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalEntries} />}
      {isSubmissionImageDialogOpen && (
        <SubmissionImageDialog
          onClose={() => setIsSubmissionImageDialogOpen(false)}
          title={submissionDialogTitle}
          imageUrl={submissionImageUrl}
        />
      )}
      {isSubmissionsDialogOpen && (
        <ContestUserProblemSubmissionsDialog
          onClose={() => setIsSubmissionsDialogOpen(false)}
          {...submissionsDialogProps}
        />
      )}
    </ContentCard>
  );
}

export default ContestScoreboardPage;
