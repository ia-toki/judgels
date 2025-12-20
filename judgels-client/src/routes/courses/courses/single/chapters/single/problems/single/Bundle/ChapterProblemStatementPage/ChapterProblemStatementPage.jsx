import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useHistory, useLocation } from 'react-router-dom';

import { ContentCard } from '../../../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { LoadingState } from '../../../../../../../../../../components/LoadingState/LoadingState';
import { ProblemWorksheetCard } from '../../../../../../../../../../components/ProblemWorksheetCard/Bundle/ProblemWorksheetCard';
import { VerdictCode } from '../../../../../../../../../../modules/api/gabriel/verdict';
import { selectCourseChapter } from '../../../../../modules/courseChapterSelectors';

import * as chapterProblemSubmissionActions from '../submissions/modules/chapterProblemSubmissionActions';

import './ChapterProblemStatementPage.scss';

export default function ChapterProblemStatementPage(props) {
  const location = useLocation();
  const history = useHistory();
  const dispatch = useDispatch();
  const chapter = useSelector(selectCourseChapter);

  const [state, setState] = useState({
    latestSubmissions: undefined,
  });

  const loadLatestSubmissions = async () => {
    if (!isInSubmissionsPath()) {
      const { progress } = props.worksheet;
      if (progress && progress.verdict !== VerdictCode.PND) {
        const resultsUrl = (location.pathname + '/submissions').replace('//', '/');
        history.replace(resultsUrl);
      }
    }

    const latestSubmissions = await dispatch(
      chapterProblemSubmissionActions.getLatestSubmissions(chapter.jid, props.worksheet.problem.alias)
    );
    setState({
      latestSubmissions,
    });
  };

  useEffect(() => {
    loadLatestSubmissions();
  }, []);

  const render = () => {
    return (
      <ContentCard className="chapter-bundle-problem-statement-page">
        {renderStatementLanguageWidget()}
        {renderStatement()}
      </ContentCard>
    );
  };

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = props.worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const widgetProps = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...widgetProps} />
      </div>
    );
  };

  const renderStatement = () => {
    const { problem, worksheet } = props.worksheet;
    if (!problem || !worksheet) {
      return <LoadingState />;
    }

    const { latestSubmissions } = state;
    if (!latestSubmissions) {
      return <LoadingState />;
    }

    const reasonNotAllowedToSubmit = isInSubmissionsPath()
      ? 'Submission received.'
      : worksheet.reasonNotAllowedToSubmit;

    const resultsUrl = (location.pathname + '/submissions').replace('//', '/');

    return (
      <ProblemWorksheetCard
        alias={problem.alias}
        latestSubmissions={latestSubmissions}
        onAnswerItem={createSubmission}
        worksheet={{ ...worksheet, reasonNotAllowedToSubmit }}
        showTitle={false}
        resultsUrl={resultsUrl}
      />
    );
  };

  const createSubmission = async (itemJid, answer) => {
    const { problem } = props.worksheet;
    return await dispatch(
      chapterProblemSubmissionActions.createItemSubmission(chapter.jid, problem.problemJid, itemJid, answer)
    );
  };

  const isInSubmissionsPath = () => {
    return (location.pathname + '/').includes('/submissions/');
  };

  return render();
}
