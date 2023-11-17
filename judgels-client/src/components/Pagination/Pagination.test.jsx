import { mount } from 'enzyme';
import { stringify } from 'query-string';
import ReactPaginate from 'react-paginate';
import createMockStore from 'redux-mock-store';
import { Provider } from 'react-redux';
import { MemoryRouter, Route } from 'react-router';
import { push } from 'connected-react-router';

import Pagination from './Pagination';

describe('Pagination', () => {
  let store;
  let wrapper;
  let onChangePage;

  const render = async pageQuery => {
    const props = {
      pageSize: 6,
      onChangePage,
    };
    const component = () => <Pagination {...props} />;

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/component' + pageQuery]}>
          <Route path="/component" component={component} />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  beforeEach(() => {
    store = createMockStore()({});
    onChangePage = jest.fn().mockReturnValue(Promise.resolve(14));
  });

  describe('when there is no data', () => {
    beforeEach(() => {
      onChangePage = jest.fn().mockReturnValue(Promise.resolve(0));
      render('');
    });

    it('does not show the helper text', () => {
      expect(wrapper.find('[data-key="pagination-helper-text"]')).toHaveLength(0);
    });
  });

  describe('when there is no page query string', () => {
    beforeEach(() => render(''));

    it('navigates to page 1', () => {
      expect(onChangePage).toBeCalledWith(1);
    });

    it('shows the helper text', () => {
      expect(wrapper.find('[data-key="pagination-helper-text"]').text()).toEqual('Showing 1..6 of 14 results');
    });
  });

  describe('when there is page query string', () => {
    beforeEach(() => render('?page=3'));

    it('navigates to that page', () => {
      expect(onChangePage).toBeCalledWith(3);
    });

    it('shows the helper text', () => {
      expect(wrapper.find('[data-key="pagination-helper-text"]').text()).toEqual('Showing 13..14 of 14 results');
    });
  });

  describe('when page changes', () => {
    beforeEach(() => render('?page=2'));

    describe('when page changes to page 1', () => {
      beforeEach(() => {
        wrapper
          .find(ReactPaginate)
          .props()
          .onPageChange({ selected: 0 });
      });

      it('clears the query string', () => {
        expect(store.getActions()).toContainEqual(push({ search: '' }));
      });
    });

    describe('when page changes to page > 1', () => {
      beforeEach(() => {
        wrapper
          .find(ReactPaginate)
          .props()
          .onPageChange({ selected: 2 });
      });

      it('pushes the query string', () => {
        const query = stringify({ page: 3 });
        expect(store.getActions()).toContainEqual(push({ search: query }));
      });
    });
  });
});
