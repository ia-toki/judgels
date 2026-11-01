// A course keeps its cover image in its description, as a first paragraph holding nothing but the image.
export function splitCourseDescription(description) {
  const { body } = new DOMParser().parseFromString(description || '', 'text/html');

  const first = body.firstChild;
  const isCover = first?.nodeName === 'P' && !first.textContent.trim();
  const image = isCover ? first.querySelector('img') : null;
  if (!image) {
    return { text: body.innerHTML };
  }

  first.remove();
  return { imageUrl: image.getAttribute('src'), text: body.innerHTML.trim() };
}
