import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useParams, useResolvedPath } from 'react-router-dom';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetActions from '../modules/problemSetActions';

export default function SingleProblemSetDataRoute() {
  const { problemSetSlug } = useParams();
  const { pathname } = useResolvedPath('');
  const dispatch = useDispatch();

  const loadProblemSet = async () => {
    const problemSet = await dispatch(problemSetActions.getProblemSetBySlug(problemSetSlug));
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, problemSet.name));
  };

  useEffect(() => {
    loadProblemSet();

    return () => {
      dispatch(problemSetActions.clearProblemSet());
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
  }, []);

  return null;
}
