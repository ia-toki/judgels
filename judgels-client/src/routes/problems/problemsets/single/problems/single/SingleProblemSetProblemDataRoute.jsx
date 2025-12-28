import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useResolvedPath } from 'react-router-dom';

import { selectProblemSet } from '../../../modules/problemSetSelectors';

import * as breadcrumbsActions from '../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetProblemActions from '../modules/problemSetProblemActions';

export default function SingleProblemSetProblemDataRoute() {
  const { problemSetSlug, problemAlias } = useParams();
  const { pathname } = useResolvedPath('');
  const dispatch = useDispatch();
  const problemSet = useSelector(selectProblemSet);

  const loadProblemSetProblem = async () => {
    if (!problemSet || problemSet.slug !== problemSetSlug) {
      return;
    }
    await dispatch(problemSetProblemActions.getProblem(problemSet.jid, problemAlias));
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, problemAlias));
  };

  useEffect(() => {
    loadProblemSetProblem();

    return () => {
      dispatch(problemSetProblemActions.clearProblem());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, [problemSet?.jid]);

  return null;
}
