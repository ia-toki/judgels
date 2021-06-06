import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import ContestEditDescriptionTab from './ContestEditDescriptionTab';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestActions from '../../../modules/contestActions';

jest.mock('../../../modules/contestActions');

describe('ContestEditDescriptionTab', () => {
  let wrapper;

  beforeEach(() => {
    contestActions.getContestDescription.mockReturnValue(() =>
      Promise.resolve({
        description: 'current description',
      })
    );
    contestActions.updateContestDescription.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestEditDescriptionTab />
        </MemoryRouter>
      </Provider>
    );
  });

  test('contest edit description tab form', async () => {
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();

    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    const description = wrapper.find('textarea[name="description"]');
    description.simulate('change', { target: { value: 'new description' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestActions.updateContestDescription).toHaveBeenCalledWith('contestJid', 'new description');
  });
});
