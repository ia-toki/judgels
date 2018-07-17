import {
  INITIAL_STATE,
  BreadcrumbsState,
  AddBreadcrumb,
  breadcrumbsReducer,
  DelBreadcrumb,
} from './breadcrumbsReducer';

describe('breadcrumbsReducer', () => {
  test('ADD', () => {
    const state: BreadcrumbsState = {
      values: [{ link: '/a', title: 'A' }],
    };
    const action = AddBreadcrumb.create({ link: '/a/b', title: 'AB' });
    const nextState: BreadcrumbsState = {
      values: [{ link: '/a', title: 'A' }, { link: '/a/b', title: 'AB' }],
    };
    expect(breadcrumbsReducer(state, action)).toEqual(nextState);
  });

  test('DEL', () => {
    const state: BreadcrumbsState = {
      values: [{ link: '/a', title: 'A' }, { link: '/a/b/c', title: 'ABC' }, { link: '/a/b', title: 'AB' }],
    };
    const action = DelBreadcrumb.create({ link: '/a/b/c' });
    const nextState: BreadcrumbsState = {
      values: [{ link: '/a', title: 'A' }, { link: '/a/b', title: 'AB' }],
    };
    expect(breadcrumbsReducer(state, action)).toEqual(nextState);
  });

  test('other actions', () => {
    const state: BreadcrumbsState = {
      values: [{ link: '/a', title: 'A' }],
    };
    expect(breadcrumbsReducer(state, { type: 'other' })).toEqual(state);
  });

  test('initial state', () => {
    expect(breadcrumbsReducer(undefined as any, { type: 'other' })).toEqual(INITIAL_STATE);
  });
});
