import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useResolvedPath } from 'react-router-dom';

import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle) {
  return InnerComponent => {
    return function WrappedComponent(props) {
      const dispatch = useDispatch();
      const { pathname } = useResolvedPath('');

      useEffect(() => {
        dispatch(PushBreadcrumb({ link: pathname, title: breadcrumbTitle }));
        return () => {
          dispatch(PopBreadcrumb({ link: pathname }));
        };
      }, []);

      return <InnerComponent {...props} />;
    };
  };
}
