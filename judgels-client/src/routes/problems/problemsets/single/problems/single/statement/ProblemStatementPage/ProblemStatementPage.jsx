import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { sendGAEvent } from '../../../../../../../../ga';
import { ProblemType } from '../../../../../../../../modules/api/sandalphon/problem';
import { selectStatementLanguage } from '../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import ProblemSetProblemBundleStatementPage from '../Bundle/ProblemStatementPage';
import ProblemSetProblemProgrammingStatementPage from '../Programming/ProblemStatementPage';

import * as problemSetProblemActions from '../../../modules/problemSetProblemActions';

export default function ProblemStatementPage() {
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);
  const statementLanguage = useSelector(selectStatementLanguage);
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const loadWorksheet = async () => {
    setState({ response: undefined });

    const response = await dispatch(
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
