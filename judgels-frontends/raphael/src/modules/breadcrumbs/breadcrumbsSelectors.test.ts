import { selectDocumentTitle, selectSortedBreadcrumbs } from './breadcrumbsSelectors';
import { AppState } from '../store';

describe('breadcrumbsSelectors', () => {
  test('selectSortedBreadcrumbs()', () => {
    const state: Partial<AppState> = {
      breadcrumbs: {
        values: [{ link: '/a', title: 'A' }, { link: '/a/b/c', title: 'ABC' }, { link: '/a/b', title: 'AB' }],
      },
    };

    const sortedBreadcrumbs = selectSortedBreadcrumbs(state as any);
    expect(sortedBreadcrumbs).toEqual([
      { link: '/a', title: 'A' },
      { link: '/a/b', title: 'AB' },
      { link: '/a/b/c', title: 'ABC' },
    ]);
  });

  test('selectDocumentTitle()', () => {
    let state: Partial<AppState> = {
      breadcrumbs: {
        values: [{ link: '/a', title: 'A' }, { link: '/a/b', title: 'AB' }],
      },
    };
    expect(selectDocumentTitle(state as any)).toEqual('AB | Judgels');

    state = {
      breadcrumbs: {
        values: [],
      },
    };
    expect(selectDocumentTitle(state as any)).toEqual('Judgels');
  });
});
