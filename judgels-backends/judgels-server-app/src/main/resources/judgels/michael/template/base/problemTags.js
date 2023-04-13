function checkStates() {
  document.querySelectorAll('.problemTag').forEach(tag => {
    if (!tag.value.includes(': ') && tag.checked) {
      tag.indeterminate = false;
      document.querySelectorAll('.problemTag').forEach(child => {
        if (tag.value !== child.value && child.value.startsWith(tag.value) && child.checked) {
          tag.indeterminate = true;
        }
      });
    }
  });
}

function checkState(tag) {
  if (tag.checked) {
    document.querySelectorAll('.problemTag').forEach(parent => {
      if (tag.value !== parent.value && tag.value.startsWith(parent.value)) {
        parent.checked = true;
      }
    });
    document.querySelectorAll('.problemTag').forEach(child => {
      if (tag.value !== child.value && child.value.startsWith(tag.value)) {
        child.checked = false;
        child.disabled = true;
      }
    });
  } else {
    document.querySelectorAll('.problemTag').forEach(child => {
      if (tag.value !== child.value && child.value.startsWith(tag.value)) {
        child.checked = false;
        child.disabled = false;
      }
    });
  }
}

document.addEventListener('DOMContentLoaded', () => {
  checkStates();

  document.querySelectorAll('.problemTag').forEach(tag => {
    tag.addEventListener('click', e => {
      checkState(e.target);
      checkStates();
    });
  });
}, false);
