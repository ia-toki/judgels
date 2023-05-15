import breadcrumbsReducer, { PushBreadcrumb, PopBreadcrumb } from './breadcrumbsReducer';

describe('breadcrumbsReducer', () => {
  test('PUSH', () => {
    const state = {
      values: [{ link: '/a', title: 'A' }],
    };
    const action = PushBreadcrumb({ link: '/a/b', title: 'AB' });
    const nextState = {
      values: [
        { link: '/a', title: 'A' },
        { link: '/a/b', title: 'AB' },
      ],
    };
    expect(breadcrumbsReducer(state, action)).toEqual(nextState);
  });

  test('POP', () => {
    const state = {
      values: [
        { link: '/a', title: 'A' },
        { link: '/a/b/c', title: 'ABC' },
        { link: '/a/b', title: 'AB' },
      ],
    };
    const action = PopBreadcrumb({ link: '/a/b/c' });
    const nextState = {
      values: [
        { link: '/a', title: 'A' },
        { link: '/a/b', title: 'AB' },
      ],
    };
    expect(breadcrumbsReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const state = {
      values: [{ link: '/a', title: 'A' }],
    };
    expect(breadcrumbsReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(breadcrumbsReducer(undefined, { type: 'other' })).toEqual({ values: [] });
  });
});
