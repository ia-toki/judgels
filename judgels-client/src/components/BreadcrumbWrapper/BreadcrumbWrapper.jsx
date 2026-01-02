import { useLocation } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';

import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle) {
  return InnerComponent => {
    return function WrappedComponent(props) {
      const { pathname } = useLocation();
      const dispatch = useDispatch();

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
