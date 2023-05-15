import { selectDocumentTitle, selectSortedBreadcrumbs } from './breadcrumbsSelectors';

describe('breadcrumbsSelectors', () => {
  test('selectSortedBreadcrumbs()', () => {
    const state = {
      breadcrumbs: {
        values: [
          { link: '/a', title: 'A' },
          { link: '/a/b/c', title: 'ABC' },
          { link: '/a/b', title: 'AB' },
        ],
      },
    };

    const sortedBreadcrumbs = selectSortedBreadcrumbs(state);
    expect(sortedBreadcrumbs).toEqual([
      { link: '/a', title: 'A' },
      { link: '/a/b', title: 'AB' },
      { link: '/a/b/c', title: 'ABC' },
    ]);
  });

  test('selectDocumentTitle()', () => {
    let state = {
      breadcrumbs: {
        values: [
          { link: '/a', title: 'A' },
          { link: '/a/b', title: 'AB' },
        ],
      },
    };
    expect(selectDocumentTitle(state)).toEqual('AB | Judgels');

    state = {
      breadcrumbs: {
        values: [],
      },
    };
    expect(selectDocumentTitle(state)).toEqual('Judgels');
  });
});
