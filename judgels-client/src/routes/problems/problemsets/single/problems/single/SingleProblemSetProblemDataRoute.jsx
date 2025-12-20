import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useRouteMatch } from 'react-router-dom';

import { selectProblemSet } from '../../../modules/problemSetSelectors';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetProblemActions from '../modules/problemSetProblemActions';

export default function SingleProblemSetProblemDataRoute() {
  const { problemSetSlug, problemAlias } = useParams();
  const match = useRouteMatch();
  const dispatch = useDispatch();
  const problemSet = useSelector(selectProblemSet);

  const loadProblemSetProblem = async () => {
    if (!problemSet || problemSet.slug !== problemSetSlug) {
      return;
    }
    await dispatch(problemSetProblemActions.getProblem(problemSet.jid, problemAlias));
    dispatch(breadcrumbsActions.pushBreadcrumb(match.url, problemAlias));
  };

  useEffect(() => {
    loadProblemSetProblem();

    return () => {
      dispatch(problemSetProblemActions.clearProblem());
      dispatch(breadcrumbsActions.popBreadcrumb(match.url));
    };
  }, [problemSet?.jid]);

  return null;
}
