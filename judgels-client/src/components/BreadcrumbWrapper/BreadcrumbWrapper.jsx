import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { useLocation, useRouteMatch } from 'react-router-dom';

import { PopBreadcrumb, PushBreadcrumb } from '../../modules/breadcrumbs/breadcrumbsReducer';

export function withBreadcrumb(breadcrumbTitle) {
  return InnerComponent => {
    return function WrappedComponent(props) {
      const dispatch = useDispatch();
      const match = useRouteMatch();
      const location = useLocation();

      useEffect(() => {
        dispatch(PushBreadcrumb({ link: match.url, title: breadcrumbTitle }));
        return () => {
          dispatch(PopBreadcrumb({ link: match.url }));
        };
      }, [dispatch, match.url]);

      return <InnerComponent location={location} {...props} />;
    };
  };
}
