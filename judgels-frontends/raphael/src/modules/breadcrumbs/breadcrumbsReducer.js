import { TypedAction, TypedReducer } from 'redoodle';

export interface Breadcrumb {
  link: string;
  title: string;
}

export interface BreadcrumbsState {
  values: Breadcrumb[];
}

export const INITIAL_STATE: BreadcrumbsState = { values: [] };

export const PushBreadcrumb = TypedAction.define('breadcrumbs/PUSH')<Breadcrumb>();

export const PopBreadcrumb = TypedAction.define('breadcrumbs/POP')<{
  link: string;
}>();

const cleanLink = link => {
  return link.replace(/\/+$/, '');
};

const createBreadcrumbsReducer = () => {
  const builder = TypedReducer.builder<BreadcrumbsState>();

  builder.withHandler(PushBreadcrumb.TYPE, (state, { link, ...rest }) => ({
    values: [...state.values, { link: cleanLink(link), ...rest }],
  }));
  builder.withHandler(PopBreadcrumb.TYPE, (state, payload) => ({
    values: state.values.filter(b => b.link !== cleanLink(payload.link)),
  }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
};

export const breadcrumbsReducer = createBreadcrumbsReducer();
