import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';

import { CourseEditDialog } from './CourseEditDialog';

const course = {
  id: 1,
  jid: 'courseJid',
  slug: 'course',
  name: 'Course',
  description: 'This is a course',
};

describe('CourseEditDialog', () => {
  let onUpdateCourse;
  let wrapper;

  beforeEach(() => {
    onUpdateCourse = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = configureMockStore()({});

    const props = {
      isOpen: true,
      course,
      onCloseDialog: jest.fn(),
      onUpdateCourse,
    };
    wrapper = mount(
      <Provider store={store}>
        <CourseEditDialog {...props} />
      </Provider>
    );
  });

  test('edit dialog form', async () => {
    const slug = wrapper.find('input[name="slug"]');
    expect(slug.prop('value')).toEqual('course');
    slug.getDOMNode().value = 'new-course';
    slug.simulate('input');

    const name = wrapper.find('input[name="name"]');
    expect(name.prop('value')).toEqual('Course');
    name.getDOMNode().value = 'New course';
    name.simulate('input');

    const description = wrapper.find('textarea[name="description"]');
    expect(description.prop('value')).toEqual('This is a course');
    description.getDOMNode().value = 'New description';
    description.simulate('input');

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onUpdateCourse).toHaveBeenCalledWith(course.jid, {
      slug: 'new-course',
      name: 'New course',
      description: 'New description',
    });
  });
});
