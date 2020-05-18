package lib.bindable;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static lib.bindable.BindableRecyclerViewAdapter.XML_MODEL;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BindableRecyclerViewAdapterTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet     attributeSetMock;
    @Mock
    View             viewMock;
    @Mock
    KeyEvent         keyEventMock;
    @Mock
    UIBindable       bindableMock;

    @Before
    public void setup()
    {
        contextMock = mock(MockScopeContext.class);
        attributeSetMock = mock(AttributeSet.class);
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,XML_MODEL)).thenReturn("test");
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,BindableRecyclerViewAdapter.XML_VIEW))
                .thenReturn("test");
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,BindableButton.XML_ON_CLICK_LISTENER))
                .thenReturn("$scope.test");
        when(attributeSetMock.getAttributeCount()).thenReturn(3);
        when(attributeSetMock.getAttributeName(0)).thenReturn(XML_MODEL);
        when(attributeSetMock.getAttributeName(1)).thenReturn(BindableRecyclerViewAdapter.XML_VIEW);
        when(attributeSetMock.getAttributeName(2)).thenReturn(BindableButton.XML_ON_CLICK_LISTENER);

        Map<String,String> map = BindableUtilities.toMap(attributeSetMock);
        when(contextMock.getModel(anyString())).thenReturn(map);

        viewMock = mock(View.class);
        keyEventMock = mock(KeyEvent.class);

        bindableMock = mock(UIBindable.class);
        when(bindableMock.getExportableAttributes()).thenReturn(attributeSetMock);
    }

    @Test
    public void onCreateViewHolder()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        Object vh = underTest.onCreateViewHolder(
                mock(ViewGroup.class),
                0
        );
        assertNull(vh);
    }

    @Test
    public void onBindViewHolder()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        // TODO this must fail as there is app specific code in this Class - fix the code
    }

    @Test
    public void getItemCount()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        int cnt = underTest.getItemCount();
        assertTrue(cnt > 0);
    }

    @Test
    public void getExportableAttributes()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() > 1);
    }

    @Test
    public void getContext()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        assertEquals(contextMock,underTest.getContext());
    }

    @Test
    public void getParent()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        assertEquals(bindableMock,underTest.getParent());
    }

    @Test
    public void getModel()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        Object model = underTest.getModel();
        assertNotNull(model);
        assertTrue(model instanceof Map);
        assertTrue(((Map)model).size() > 1);
    }

    @Test
    public void modelChanged()
    {
        BindableRecyclerViewAdapter underTest = spy(new BindableRecyclerViewAdapter(
                contextMock, bindableMock));

        underTest.modelChanged(contextMock);

        verify(underTest).notifyDataSetChanged();
    }

    @Test
    public void onClick()
    {
        MockScopeContext contextSpy = spy(contextMock);
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextSpy, bindableMock);

        underTest.onClick(viewMock);
        verify(contextSpy).test(any(UIBindable.class));
    }

    @Test
    public void getScope()
    {
        BindableRecyclerViewAdapter underTest = new BindableRecyclerViewAdapter(
                contextMock, bindableMock);

        assertEquals(contextMock,underTest.getScope());
    }
}