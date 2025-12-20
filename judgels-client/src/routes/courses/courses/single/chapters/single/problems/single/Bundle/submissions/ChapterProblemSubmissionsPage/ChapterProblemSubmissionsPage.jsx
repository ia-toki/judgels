import { Intent } from '@blueprintjs/core';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';

import { ButtonLink } from '../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorialCard } from '../../../../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemEditorialCard/ProblemEditorialCard';
import { SubmissionDetails } from '../../../../../../../../../../../components/SubmissionDetails/Bundle/SubmissionDetails/SubmissionDetails';
import { selectMaybeUserJid } from '../../../../../../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../../modules/courseChapterSelectors';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

import './ChapterProblemSubmissionsPage.scss';

export default function ChapterProblemSubmissionsPage({ worksheet, renderNavigation }) {
  const { problemAlias } = useParams();
  const dispatch = useDispatch();
  const userJid = useSelector(selectMaybeUserJid);
  const course = useSelector(selectCourse);
  const chapter = useSelector(selectCourseChapter);
  const language = useSelector(selectStatementLanguage);

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

    const response = await dispatch(
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
          to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}`}
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
