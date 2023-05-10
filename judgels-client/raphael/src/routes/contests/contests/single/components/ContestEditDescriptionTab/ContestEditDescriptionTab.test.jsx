import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
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
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <ContestEditDescriptionTab />
      </Provider>
    );
  });

  test('contest edit description tab form', async () => {
    const button = wrapper.find('button');
    button.simulate('click');

    const description = wrapper.find('textarea[name="description"]');
    expect(description.prop('value')).toEqual('current description');
    description.getDOMNode().value = 'new description';
    description.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(contestActions.updateContestDescription).toHaveBeenCalledWith('contestJid', 'new description');
  });
});
