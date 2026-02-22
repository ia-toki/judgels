import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { sendGAEvent } from '../../../../../../../../ga';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
  problemSetProblemWorksheetQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import ProblemSetProblemBundleStatementPage from '../Bundle/ProblemStatementPage';
import ProblemSetProblemProgrammingStatementPage from '../Programming/ProblemStatementPage';

export default function ProblemStatementPage() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(
    problemSetProblemWorksheetQueryOptions(problemSet.jid, problem.alias, { language: statementLanguage })
  );

  useEffect(() => {
    if (response) {
      sendGAEvent({ category: 'Problems', action: 'View problemset problem', label: problemSet.name });
      sendGAEvent({
        category: 'Problems',
        action: 'View problem',
        label: problemSet.name + ': ' + problem.alias,
      });
    }
  }, [response]);

  if (!response) {
    return <LoadingState />;
  }

  const { problem: problemData } = response;
  if (problemData.type === ProblemType.Programming) {
    return <ProblemSetProblemProgrammingStatementPage worksheet={response} />;
  } else {
    return <ProblemSetProblemBundleStatementPage worksheet={response} />;
  }
}
