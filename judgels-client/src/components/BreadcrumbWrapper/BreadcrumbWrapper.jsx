import { useEffect } from 'react';
import { useDispatch } from 'react-redux';

import { useBreadcrumbsPath } from '../../hooks/useBreadcrumbsPath';
import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle) {
  return InnerComponent => {
    return function WrappedComponent(props) {
      const dispatch = useDispatch();
      const breadcrumbsPath = useBreadcrumbsPath();

      useEffect(() => {
        dispatch(PushBreadcrumb({ link: breadcrumbsPath, title: breadcrumbTitle }));
        return () => {
          dispatch(PopBreadcrumb({ link: breadcrumbsPath }));
        };
      }, []);

      return <InnerComponent {...props} />;
    };
  };
}
