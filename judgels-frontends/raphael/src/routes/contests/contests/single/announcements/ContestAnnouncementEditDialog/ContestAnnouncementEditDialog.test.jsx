import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import { ContestAnnouncementEditDialog } from './ContestAnnouncementEditDialog';

describe('ContestAnnouncementEditDialog', () => {
  let onUpdateAnnouncement;
  let wrapper;

  const announcement = {
    jid: 'announcementJid123',
    title: 'Snack',
    content: 'Snack is provided.',
    status: ContestAnnouncementStatus.Published,
  };

  beforeEach(() => {
    onUpdateAnnouncement = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const onToggleEditDialog = () => {
      return;
    };

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
      contest: { jid: 'contestJid' },
      announcement,
      onToggleEditDialog,
      onUpdateAnnouncement,
    };
    wrapper = mount(
      <Provider store={store}>
        <ContestAnnouncementEditDialog {...props} />
      </Provider>
    );
  });

  test('form', () => {
    // TODO(fushar): make this work
    // See https://github.com/FezVrasta/popper.js/issues/478

    // const status = wrapper.find('button[data-key="status"]');
    // status.simulate('click');

    const title = wrapper.find('input[name="title"]');
    expect(title.prop('value')).toEqual('Snack');
    title.simulate('change', { target: { value: 'Snack [edited]' } });

    const content = wrapper.find('textarea[name="content"]');
    expect(content.prop('value')).toEqual('Snack is provided.');
    content.simulate('change', { target: { value: 'Snack is NOT provided.' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateAnnouncement).toHaveBeenCalledWith('contestJid', 'announcementJid123', {
      title: 'Snack [edited]',
      content: 'Snack is NOT provided.',
      status: ContestAnnouncementStatus.Published,
    });
  });
});
