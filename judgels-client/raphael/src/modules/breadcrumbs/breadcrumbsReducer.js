const initialState = { values: [] };

export function PushBreadcrumb({ link, title }) {
  return {
    type: 'breadcrumbs/PUSH',
    payload: { link, title },
  };
}

export function PopBreadcrumb({ link }) {
  return {
    type: 'breadcrumbs/POP',
    payload: { link },
  };
}
const cleanLink = link => {
  return link.replace(/\/+$/, '');
};

export default function breadcrumbsReducer(state = initialState, action) {
  switch (action.type) {
    case 'breadcrumbs/PUSH':
      const { link, title } = action.payload;
      return { values: [...state.values, { link: cleanLink(link), title }] };
    case 'breadcrumbs/POP':
      return { values: state.values.filter(b => b.link !== cleanLink(action.payload.link)) };
    default:
      return state;
  }
}
