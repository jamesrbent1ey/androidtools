package lib.bindable;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BindableAdapterTest
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
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,
                BindableRecyclerViewAdapter.XML_MODEL)).thenReturn("test");
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,
                "test")).thenReturn("test");
        when(attributeSetMock.getAttributeCount()).thenReturn(2);
        when(attributeSetMock.getAttributeName(0)).thenReturn(BindableRecyclerViewAdapter.XML_MODEL);
        when(attributeSetMock.getAttributeName(1)).thenReturn("test");
        //Map<String,String> map = BindableUtilities.toMap(attributeSetMock);

        LinkedList list = new LinkedList();
        list.add("test1");
        list.add("test2");
        when(contextMock.getModel(anyString())).thenReturn(list);

        viewMock = mock(View.class);
        keyEventMock = mock(KeyEvent.class);

        bindableMock = mock(UIBindable.class);
        when(bindableMock.getExportableAttributes()).thenReturn(attributeSetMock);
    }

    @Test
    public void getCount()
    {
        BindableAdapter underTest = new BindableAdapter(contextMock,bindableMock);
        int cnt = underTest.getCount();
        assertTrue(cnt > 0);
    }

    @Test
    public void getItem()
    {
        BindableAdapter underTest = new BindableAdapter(contextMock,bindableMock);
        assertEquals("test2", underTest.getItem(1));
    }

    @Test
    public void getItemId()
    {
        BindableAdapter underTest = new BindableAdapter(contextMock,bindableMock);
        assertTrue(underTest.getItemId(0)  != 0);
    }

    @Test
    public void getView()
    {
        BindableAdapter underTest = new BindableAdapter(contextMock,bindableMock);
        Object v = underTest.getView(0, null, mock(ViewGroup.class));
        assertNotNull(v);
    }

    @Test
    public void getExportableAttributes()
    {
        BindableAdapter underTest = new BindableAdapter(contextMock,bindableMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() > 1);
    }

    @Test
    public void getScope()
    {
        BindableAdapter underTest = new BindableAdapter(contextMock,bindableMock);
        assertEquals(contextMock,underTest.getScope());
    }

    @Test
    public void modelChanged()
    {
        MockScopeContext contextSpy = spy(contextMock);
        BindableAdapter underTest = new BindableAdapter(contextSpy,
                bindableMock);

        underTest.modelChanged(contextSpy);
        verify(contextSpy,atLeastOnce()).getModel(anyString());
    }
}