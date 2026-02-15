import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { sendGAEvent } from '../../../../../../../../ga';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import { callAction } from '../../../../../../../../modules/callAction';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import { useWebPrefs } from '../../../../../../../../modules/webPrefs';
import ProblemSetProblemBundleStatementPage from '../Bundle/ProblemStatementPage';
import ProblemSetProblemProgrammingStatementPage from '../Programming/ProblemStatementPage';

import * as problemSetProblemActions from '../../../modules/problemSetProblemActions';

export default function ProblemStatementPage() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));
  const { statementLanguage } = useWebPrefs();

  const [state, setState] = useState({
    response: undefined,
  });

  const loadWorksheet = async () => {
    setState({ response: undefined });

    const response = await callAction(
      problemSetProblemActions.getProblemWorksheet(problemSet.jid, problem.alias, statementLanguage)
    );

    setState({ response });

    sendGAEvent({ category: 'Problems', action: 'View problemset problem', label: problemSet.name });
    sendGAEvent({
      category: 'Problems',
      action: 'View problem',
      label: problemSet.name + ': ' + problem.alias,
    });
  };

  useEffect(() => {
    loadWorksheet();
  }, [statementLanguage]);

  const render = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }
    const { problem } = response;
    if (problem.type === ProblemType.Programming) {
      return <ProblemSetProblemProgrammingStatementPage worksheet={response} />;
    } else {
      return <ProblemSetProblemBundleStatementPage worksheet={response} />;
    }
  };

  return render();
}
