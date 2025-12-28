import { useLocation, useParams } from 'react-router';

/**
 * Hook to get the breadcrumbs path for the current route, excluding any splat portion.
 *
 * For example, if the route is "/contests/:contestSlug/*" and the current location
 * is "/contests/my-contest/problems/A", this hook returns "/contests/my-contest".
 *
 * This is used by the breadcrumbs system to determine the unique path for each breadcrumb.
 */
export function useBreadcrumbsPath() {
  const location = useLocation();
  const params = useParams();

  const splatValue = params['*'] || '';
  if (splatValue) {
    return location.pathname.slice(0, location.pathname.length - splatValue.length - 1);
  }
  return location.pathname;
}
