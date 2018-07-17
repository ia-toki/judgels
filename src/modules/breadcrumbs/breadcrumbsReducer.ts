import { TypedAction, TypedReducer } from 'redoodle';

export interface Breadcrumb {
  link: string;
  title: string;
}

export interface BreadcrumbsState {
  values: Breadcrumb[];
}

export const INITIAL_STATE: BreadcrumbsState = { values: [] };

export const AddBreadcrumb = TypedAction.define('breadcrumbs/ADD')<Breadcrumb>();

export const DelBreadcrumb = TypedAction.define('breadcrumbs/DEL')<{
  link: string;
}>();

const createBreadcrumbsReducer = () => {
  const builder = TypedReducer.builder<BreadcrumbsState>();

  builder.withHandler(AddBreadcrumb.TYPE, (state, payload) => ({
    values: [...state.values, payload],
  }));
  builder.withHandler(DelBreadcrumb.TYPE, (state, payload) => ({
    values: state.values.filter(b => b.link !== payload.link),
  }));
  builder.withDefaultHandler(state => (state !== undefined ? state : INITIAL_STATE));

  return builder.build();
};

export const breadcrumbsReducer = createBreadcrumbsReducer();
