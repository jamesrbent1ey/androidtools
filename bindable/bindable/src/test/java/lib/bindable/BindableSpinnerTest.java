package lib.bindable;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static lib.bindable.BindableSpinner.XML_ON_ITEM_SELECTED_LISTENER;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BindableSpinnerTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet     attributeSetMock;
    @Mock
    View             viewMock;
    @Mock
    KeyEvent         keyEventMock;

    @Before
    public void setup()
    {
        contextMock = mock(MockScopeContext.class);
        attributeSetMock = mock(MockAttributeSet.class);
        when(((MockAttributeSet) attributeSetMock).getAttributeNamespace(anyInt())).thenReturn(UIBindable.NAMESPACE);
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,
                XML_ON_ITEM_SELECTED_LISTENER))
                .thenReturn("$scope.testSelected");
        when(attributeSetMock.getAttributeValue(UIBindable.NAMESPACE,"test")).thenReturn("test");
        when(attributeSetMock.getAttributeCount()).thenReturn(2);
        when(attributeSetMock.getAttributeName(0)).thenReturn(XML_ON_ITEM_SELECTED_LISTENER);
        when(attributeSetMock.getAttributeValue(0)).thenReturn("$scope.testSelected");
        when(attributeSetMock.getAttributeName(1)).thenReturn("test");
        when(attributeSetMock.getAttributeValue(1)).thenReturn("test");
        Map<String,String> map = BindableUtilities.toMap(attributeSetMock);

        when(contextMock.getModel(anyString())).thenReturn(map);

        viewMock = mock(View.class);
        keyEventMock = mock(KeyEvent.class);
    }

    @Test
    public void getExportableAttributes()
    {
        BindableSpinner underTest = new BindableSpinner(contextMock, attributeSetMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() > 1);
    }

    @Test
    public void getScope()
    {
        BindableSpinner underTest = new BindableSpinner(contextMock, attributeSetMock);
        assertEquals(contextMock,underTest.getScope());
    }

    @Test
    public void onItemSelected()
    {
        MockScopeContext contextSpy = spy(contextMock);
        BindableSpinner underTest = new BindableSpinner(contextSpy, attributeSetMock);

        AdapterView adapterViewMock = mock(AdapterView.class);
        underTest.onItemSelected(adapterViewMock,
                viewMock,
                0,
                0
        );
        verify(contextSpy).testSelected(any(UIBindable.class),
                any(AdapterView.class),anyInt());
    }

    @Test
    public void onNothingSelected()
    {
        MockScopeContext contextSpy = spy(contextMock);
        BindableSpinner underTest = new BindableSpinner(contextSpy, attributeSetMock);

        AdapterView adapterViewMock = mock(AdapterView.class);
        underTest.onItemSelected(adapterViewMock,
                viewMock,
                0,
                0
        );
        verify(contextSpy).testSelected(any(UIBindable.class),
                any(AdapterView.class),anyInt());
    }
}