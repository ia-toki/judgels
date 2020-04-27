import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetEditDialog } from './ProblemSetEditDialog';

const problemSet: ProblemSet = {
  id: 1,
  jid: 'problemSetJid',
  slug: 'problemSet',
  name: 'ProblemSet',
  archiveJid: 'JIDARCH',
  description: 'This is a problemSet',
};

describe('ProblemSetEditDialog', () => {
  let onUpdateProblemSet: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  beforeEach(() => {
    onUpdateProblemSet = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }), applyMiddleware(thunk));

    const props = {
      isOpen: true,
      problemSet,
      archiveSlug: 'archive',
      onCloseDialog: jest.fn(),
      onUpdateProblemSet,
    };
    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ProblemSetEditDialog {...props} />
        </MemoryRouter>
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const slug = wrapper.find('input[name="slug"]');
    slug.simulate('change', { target: { value: 'new-problemSet' } });

    const name = wrapper.find('input[name="name"]');
    name.simulate('change', { target: { value: 'New problemSet' } });

    const archiveSlug = wrapper.find('input[name="archiveSlug"]');
    archiveSlug.simulate('change', { target: { value: 'New archive' } });

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'New description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateProblemSet).toHaveBeenCalledWith(problemSet.jid, {
      slug: 'new-problemSet',
      name: 'New problemSet',
      archiveSlug: 'New archive',
      description: 'New description',
    });
  });
});
