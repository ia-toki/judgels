import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import StatementLanguageWidget from '../../../../../../../../components/LanguageWidget/StatementLanguageWidget';
import { ProblemWorksheetCard } from '../../../../../../../../components/ProblemWorksheetCard/Programming/ProblemWorksheetCard';
import { sendGAEvent } from '../../../../../../../../ga';
import { getGradingLanguageFamily } from '../../../../../../../../modules/api/gabriel/language.js';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';

import * as problemSetSubmissionActions from '../../submissions/modules/problemSetSubmissionActions';

export default function ProblemStatementPage({ worksheet }) {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(token, problemSet.jid, problemAlias));
  const { gradingLanguage, setGradingLanguage } = useWebPrefs();

  const renderStatementLanguageWidget = () => {
    const { defaultLanguage, languages } = worksheet;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage: defaultLanguage,
      statementLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <StatementLanguageWidget {...props} />
      </div>
    );
  };

  const renderStatement = () => {
    return (
      <ProblemWorksheetCard
        worksheet={worksheet.worksheet}
        onSubmit={createSubmission}
        gradingLanguage={gradingLanguage}
      />
    );
  };

  const createSubmission = async data => {
    setGradingLanguage(data.gradingLanguage);

    sendGAEvent({ category: 'Problems', action: 'Submit problemset problem', label: problemSet.name });
    sendGAEvent({
      category: 'Problems',
      action: 'Submit problem',
      label: problemSet.name + ': ' + problem.alias,
    });
    if (getGradingLanguageFamily(data.gradingLanguage)) {
      sendGAEvent({
        category: 'Problems',
        action: 'Submit language',
        label: getGradingLanguageFamily(data.gradingLanguage),
      });
    }

    return await dispatch(
      problemSetSubmissionActions.createSubmission(
        problemSet.slug,
        problemSet.jid,
        problem.alias,
        worksheet.problem.problemJid,
        data
      )
    );
  };

  return (
    <ContentCard>
      {renderStatementLanguageWidget()}
      {renderStatement()}
    </ContentCard>
  );
}
