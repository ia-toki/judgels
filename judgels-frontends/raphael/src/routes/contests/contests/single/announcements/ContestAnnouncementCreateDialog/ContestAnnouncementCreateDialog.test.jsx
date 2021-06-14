import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { ContestAnnouncementCreateDialog } from './ContestAnnouncementCreateDialog';

describe('ContestAnnouncementCreateDialog', () => {
  let onCreateAnnouncement;
  let wrapper;

  beforeEach(() => {
    onCreateAnnouncement = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      onCreateAnnouncement,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestAnnouncementCreateDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    const button = wrapper.find('button');
    button.simulate('click');

    wrapper.update();

    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = wrapper.find('button[data-key="status"]');
    // status.simulate('click');

    const title = wrapper.find('input[name="title"]');
    title.getDOMNode().value = 'Snack';
    title.simulate('input');

    const content = wrapper.find('textarea[name="content"]');
    content.getDOMNode().value = 'Snack is provided.';
    content.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onCreateAnnouncement).toHaveBeenCalledWith('contestJid', {
      title: 'Snack',
      content: 'Snack is provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});
