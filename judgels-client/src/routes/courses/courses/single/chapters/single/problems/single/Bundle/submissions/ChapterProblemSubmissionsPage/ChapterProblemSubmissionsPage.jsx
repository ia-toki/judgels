import { Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ButtonLink } from '../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorialCard } from '../../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemEditorialCard/ProblemEditorialCard';
import { SubmissionDetails } from '../../../../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { callAction } from '../../../../../../../../../../../modules/callAction';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../../modules/queries/course';
import { useSession } from '../../../../../../../../../../../modules/session';
import { useWebPrefs } from '../../../../../../../../../../../modules/webPrefs';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

import './ChapterProblemSubmissionsPage.scss';

export default function ChapterProblemSubmissionsPage({ worksheet, renderNavigation }) {
  const { courseSlug, chapterAlias, problemAlias } = useParams({ strict: false });
  const { token, user } = useSession();
  const userJid = user?.jid;
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));
  const { statementLanguage: language } = useWebPrefs();

  const [state, setState] = useState({
    config: undefined,
    profile: undefined,
    problemSummaries: undefined,
  });

  const refreshSubmissions = async () => {
    if (!userJid) {
      setState(prevState => ({ ...prevState, problemSummaries: [] }));
      return;
    }

    const response = await callAction(
      chapterProblemSubmissionActions.getSubmissionSummary(chapter.jid, problemAlias, language)
    );

    const problemSummaries = response.config.problemJids.map(problemJid => ({
      name: response.problemNamesMap[problemJid] || '-',
      alias: response.problemAliasesMap[chapter.jid + '-' + problemJid] || '-',
      itemJids: response.itemJidsByProblemJid[problemJid],
      submissionsByItemJid: response.submissionsByItemJid,
      canViewGrading: true,
      canManage: false,
      itemTypesMap: response.itemTypesMap,
    }));

    setState({ config: response.config, profile: response.profile, problemSummaries });
  };

  useEffect(() => {
    refreshSubmissions();
  }, []);

  const render = () => {
    return (
      <ContentCard className="chapter-bundle-problem-submissions-page">
        <h3 className="heading-with-button-action">Results</h3>
        <ButtonLink
          small
          intent={Intent.PRIMARY}
          to={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problemAlias}`}
        >
          Retake
        </ButtonLink>
        <hr />
        {renderResults()}
        {renderEditorial()}
        {renderNavigationSection()}
      </ContentCard>
    );
  };

  const renderResults = () => {
    const { problemSummaries } = state;
    if (!problemSummaries) {
      return <LoadingState />;
    }
    if (problemSummaries.length === 0) {
      return <small>No quizzes.</small>;
    }
    return (
      <>
        {problemSummaries.map(props => (
          <SubmissionDetails key={props.alias} {...props} showTitle={false} />
        ))}
      </>
    );
  };

  const renderEditorial = () => {
    const { problem, editorial } = worksheet;
    if (!editorial) {
      return null;
    }
    return (
      <div className="chapter-problem-editorial">
        <hr />
        <ProblemEditorialCard
          alias={problem.alias}
          statement={worksheet.worksheet.statement}
          editorial={editorial}
          showTitle={false}
        />
      </div>
    );
  };

  const renderNavigationSection = () => {
    return <div className="chapter-problem-navigation">{renderNavigation({ hidePrev: true })}</div>;
  };

  return render();
}
