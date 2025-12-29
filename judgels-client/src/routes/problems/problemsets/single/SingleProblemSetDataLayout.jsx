import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useParams } from 'react-router';

import { useBreadcrumbsPath } from '../../../../hooks/useBreadcrumbsPath';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as problemSetActions from '../modules/problemSetActions';

export default function SingleProblemSetDataLayout() {
  const { problemSetSlug } = useParams();
  const pathname = useBreadcrumbsPath();
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
